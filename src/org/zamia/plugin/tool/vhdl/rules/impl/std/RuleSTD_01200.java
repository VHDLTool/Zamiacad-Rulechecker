package org.zamia.plugin.tool.vhdl.rules.impl.std;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.w3c.dom.Element;
import org.zamia.SourceLocation;
import org.zamia.ZamiaProject;
import org.zamia.plugin.tool.vhdl.EntityException;
import org.zamia.plugin.tool.vhdl.HdlArchitecture;
import org.zamia.plugin.tool.vhdl.HdlEntity;
import org.zamia.plugin.tool.vhdl.HdlFile;
import org.zamia.plugin.tool.vhdl.ReportFile;
import org.zamia.plugin.tool.vhdl.manager.ArchitectureManager;
import org.zamia.plugin.tool.vhdl.manager.PackageBodyManager;
import org.zamia.plugin.tool.vhdl.rules.RuleE;
import org.zamia.plugin.tool.vhdl.rules.RuleResult;
import org.zamia.plugin.tool.vhdl.rules.impl.Rule;
import org.zamia.plugin.tool.vhdl.rules.impl.SonarQubeRule;
import org.zamia.util.Pair;
import org.zamia.vhdl.ast.Architecture;
import org.zamia.vhdl.ast.AssociationList;
import org.zamia.vhdl.ast.Block;
import org.zamia.vhdl.ast.BlockDeclarativeItem;
import org.zamia.vhdl.ast.ConcurrentAssertion;
import org.zamia.vhdl.ast.ConcurrentProcedureCall;
import org.zamia.vhdl.ast.ConcurrentSignalAssignment;
import org.zamia.vhdl.ast.ConcurrentStatement;
import org.zamia.vhdl.ast.GenerateStatement;
import org.zamia.vhdl.ast.InstantiatedUnit;
import org.zamia.vhdl.ast.InterfaceDeclaration;
import org.zamia.vhdl.ast.NullStatement;
import org.zamia.vhdl.ast.PackageBody;
import org.zamia.vhdl.ast.ReturnStatement;
import org.zamia.vhdl.ast.SequenceOfStatements;
import org.zamia.vhdl.ast.SequentialAssert;
import org.zamia.vhdl.ast.SequentialCase;
import org.zamia.vhdl.ast.SequentialExit;
import org.zamia.vhdl.ast.SequentialIf;
import org.zamia.vhdl.ast.SequentialLoop;
import org.zamia.vhdl.ast.SequentialNextStatement;
import org.zamia.vhdl.ast.SequentialProcedureCall;
import org.zamia.vhdl.ast.SequentialProcess;
import org.zamia.vhdl.ast.SequentialReport;
import org.zamia.vhdl.ast.SequentialSignalAssignment;
import org.zamia.vhdl.ast.SequentialStatement;
import org.zamia.vhdl.ast.SequentialVariableAssignment;
import org.zamia.vhdl.ast.SequentialWait;
import org.zamia.vhdl.ast.SubProgram;
import org.zamia.vhdl.ast.VHDLNode;
import org.zamia.vhdl.ast.VHDLPackage;

public class RuleSTD_01200 extends Rule {
	
	private int currentLine = 0;
	private int targetLine = 0;
	private int targetCol = 0;
	private ReportFile reportFile = new ReportFile(this);
	
	private String entityId;
	private String architectureId;

	public RuleSTD_01200() {
		super(RuleE.STD_01200);
	}

	@Override
	public Pair<Integer, RuleResult> Launch(ZamiaProject zPrj, String ruleId, ParameterSource parameterSource) {
		initializeRule(parameterSource, ruleId);
		
		Pair<Integer, RuleResult> result = null;
		if (reportFile.initialize()) {
			try {
				Map<String, HdlFile> hdlFiles = ArchitectureManager.getArchitecture();
				for (Entry<String, HdlFile> hdlFile : hdlFiles.entrySet()) {
					List<HdlEntity> hdlEntities = hdlFile.getValue().getListHdlEntity();
					for (HdlEntity hdlEntity : hdlEntities) {
						// TODO do something in entity?
						entityId = hdlEntity.getEntity().getId();
						List<HdlArchitecture> hdlArchitectures = hdlEntity.getListHdlArchitecture();
						for (HdlArchitecture hdlArchitecture : hdlArchitectures) {
							// do operations in each architecture
							Architecture architecture = hdlArchitecture.getArchitecture();
							architectureId = architecture.getId();
							// declarative items
							for (int i = 0; i < architecture.getNumDeclarations(); i++) {
								SourceLocation location = architecture.getDeclaration(i).getLocation();
								logger.info("[Declarative item] item at %d", location.fLine);
								expandDeclarativeItem(architecture.getDeclaration(i));
							}
							// concurrent statements
							for (int i = 0; i < architecture.getNumConcurrentStatements(); i++) {
								ConcurrentStatement cStatement = architecture.getConcurrentStatement(i);
								logger.info("concurrent statement: %s in %s at %d", cStatement.getLabel(), cStatement.getLocation().fSF.getFileName(), cStatement.getLocation().fLine);
								expandConcurrentStatement(cStatement);
							}
						} 
					}
				}
				entityId = architectureId = "";
				hdlFiles = PackageBodyManager.getPackageBody();
				for (Entry<String, HdlFile> hdlFile : hdlFiles.entrySet()) {
					// do operations in each package
					List<VHDLPackage> packages = hdlFile.getValue().getListHdlPackage();
					for (VHDLPackage vhdlPackage : packages) {
						for (int i = 0; i < vhdlPackage.getNumDeclarations(); i++) {
							SourceLocation location = vhdlPackage.getDeclaration(i).getLocation();
							logger.info("[Declarative item] item at %d", location.fLine);
							expandDeclarativeItem(vhdlPackage.getDeclaration(i));
						}
					}
					// do operations in each package body
					List<PackageBody> packageBodies = hdlFile.getValue().getListPackageBody();
					for (PackageBody packageBody : packageBodies) {
						for (int i = 0; i < packageBody.getNumDeclarations(); i++) {
							SourceLocation location = packageBody.getDeclaration(i).getLocation();
							logger.info("[Declarative item] item at %d", location.fLine);
							expandDeclarativeItem(packageBody.getDeclaration(i));
						}
					}
				}
			} catch (EntityException e) {
				LogNeedBuild();
				return new Pair<>(NO_BUILD, null);
			}
			result = reportFile.save();
		}
		
		
		return result;
	}
	
	private void checkViolation(SourceLocation location) {
		int line = location.fLine;
		if(currentLine != line) {
			currentLine = line;
		} else {
			if (currentLine != targetLine) {
				targetLine = currentLine;
				Element element = reportFile.addViolation(location, entityId, architectureId);
				reportFile.addSonarTags(element,
						SonarQubeRule.SONAR_ERROR_STD_01200,
						new Object[] {targetLine},
						SonarQubeRule.SONAR_MSG_STD_01200, null);
			}
		}
	}
	
	private void checkInterfaceViolation(InterfaceDeclaration interfaceDeclaration) {
		int line = interfaceDeclaration.getLocation().fLine;
		if(currentLine != line) {
			currentLine = line;
			targetCol = interfaceDeclaration.getType().getLocation().fCol;
		} else {
			if (interfaceDeclaration.getType().getLocation().fCol != targetCol && currentLine != targetLine) {
				targetLine = currentLine;
				Element element = reportFile.addViolation(interfaceDeclaration.getLocation(), entityId, architectureId);
				reportFile.addSonarTags(element,
						SonarQubeRule.SONAR_ERROR_STD_01200,
						new Object[] {line},
						SonarQubeRule.SONAR_MSG_STD_01200, null);
			}
		}
	}
	
	private void expandDeclarativeItem(BlockDeclarativeItem item) {
		if (item instanceof SubProgram) {
			SubProgram subProgram = (SubProgram) item;
			for (int i = 0; i < subProgram.getNumInterfaces(); i++) {
				checkInterfaceViolation(subProgram.getInterface(i));
			}
			if (subProgram.getChild(0) != null) {
				checkViolation(subProgram.getChild(0).getLocation());
			}
			for (int i = 0; i < subProgram.getNumDeclarations(); i++) {
				expandDeclarativeItem(subProgram.getDeclaration(i));
			}
			SequenceOfStatements statements = subProgram.getCode();
			if (statements != null) {
				for (int j = 0; j < statements.getNumStatements(); j++) {
					SequentialStatement statement = statements.getStatement(j);
					expandSequentialStatement(statement);
				}
			}
		} else {
			checkViolation(item.getLocation());
		}
	}
	
	private void expandConcurrentStatement(ConcurrentStatement concurrentStatement) {
		if (concurrentStatement instanceof SequentialProcess) {
			SequentialProcess process = (SequentialProcess) concurrentStatement;
			checkViolation(process.getLocation());
			for (int i = 0; i < process.getNumDeclarations(); i++) {
				logger.info("[Sequential] Process declaration item at %s", process.getDeclaration(i).getLocation().fLine);
				checkViolation(process.getDeclaration(i).getLocation());
			}
			SequenceOfStatements statements = (SequenceOfStatements) process.getChild(0);
			if (statements != null) {
				for (int j = 0; j < statements.getNumStatements(); j++) {
					SequentialStatement statement = statements.getStatement(j);
					expandSequentialStatement(statement);
				}
			}
		} else if (concurrentStatement instanceof Block) {
			logger.info("[Concurrent] Block statement at %d", concurrentStatement.getLocation().fLine);
			// TODO
		} else if (concurrentStatement instanceof ConcurrentAssertion) {
			logger.info("[Concurrent] Assert statement at %d", concurrentStatement.getLocation().fLine);
			checkViolation(concurrentStatement.getLocation());
		} else if (concurrentStatement instanceof ConcurrentProcedureCall) {
			logger.info("[Concurrent] Procedure call statement at %d", concurrentStatement.getLocation().fLine);
			checkViolation(concurrentStatement.getLocation());
		} else if (concurrentStatement instanceof ConcurrentSignalAssignment) {
			logger.info("[Concurrent] Signal assignment statement at %d", concurrentStatement.getLocation().fLine);
			checkViolation(concurrentStatement.getLocation());
		} else if (concurrentStatement instanceof GenerateStatement) {
			logger.info("[Concurrent] Generate statement at %d", concurrentStatement.getLocation().fLine);
			GenerateStatement generateStatement = (GenerateStatement) concurrentStatement;
			checkViolation(generateStatement.getLocation());
			for (int i = 0; i < generateStatement.getNumChildren() - 2; i++) {
				VHDLNode node = generateStatement.getChild(i);
				if (node instanceof BlockDeclarativeItem) {
					expandDeclarativeItem((BlockDeclarativeItem) node);
				} else if (node instanceof ConcurrentStatement) {
					expandConcurrentStatement((ConcurrentStatement) node);
				}
			}
		} else if (concurrentStatement instanceof InstantiatedUnit) {
			logger.info("[Concurrent] Instantiated unit statement at %d", concurrentStatement.getLocation().fLine);
			InstantiatedUnit instantiatedUnit = (InstantiatedUnit) concurrentStatement;
			AssociationList mapList = instantiatedUnit.getGMS();
			if (mapList != null) {
				for (int i = 0; i < mapList.getNumAssociations(); i++) {
					checkViolation(mapList.getAssociation(i).getLocation());
				}
			}
			mapList = instantiatedUnit.getPMS();
			if (mapList != null) {
				for (int i = 0; i < mapList.getNumAssociations(); i++) {
					checkViolation(mapList.getAssociation(i).getLocation());
				}
			}
		} else {
			logger.info("[Concurrent] Error at %d", concurrentStatement.getLocation().fLine);
			checkViolation(concurrentStatement.getLocation());
		}
	}
	
	private void expandSequentialStatement(SequentialStatement sequentialStatement) {
		if (sequentialStatement instanceof NullStatement) {
			logger.info("[Sequence] Null statement at %d", sequentialStatement.getLocation().fLine);
			checkViolation(sequentialStatement.getLocation());
		} else if (sequentialStatement instanceof ReturnStatement) {
			logger.info("[Sequence] Return statement at %d", sequentialStatement.getLocation().fLine);
			checkViolation(sequentialStatement.getLocation());
		} else if (sequentialStatement instanceof SequentialAssert) {
			logger.info("[Sequence] Assert statement at %d", sequentialStatement.getLocation().fLine);
			checkViolation(sequentialStatement.getLocation()); 
		} else if (sequentialStatement instanceof SequentialCase) {
			SequentialCase caseStatement = (SequentialCase) sequentialStatement;
			logger.info("[Sequence] Case statement, case at %d", caseStatement.getExpr().getLocation().fLine);
			checkViolation(caseStatement.getExpr().getLocation());
			for (int i = 1; i < caseStatement.getNumChildren(); i++) {
				for (int j = 1; j < caseStatement.getChild(i).getNumChildren(); j++) {
					if (caseStatement.getChild(i).getChild(j) == null) {
						logger.info("[Sequence] Case statement, when others at %d", caseStatement.getChild(i).getLocation().fLine);
						checkViolation(caseStatement.getChild(i).getLocation());
					} else {
						logger.info("[Sequence] Case statement, when at %d", caseStatement.getChild(i).getChild(j).getLocation().fLine);
						checkViolation(caseStatement.getChild(i).getChild(j).getLocation());
					}
				}
				SequenceOfStatements statements = (SequenceOfStatements) caseStatement.getChild(i).getChild(0);
				if (statements != null) {
					for (int j = 0; j < statements.getNumStatements(); j++) {
						expandSequentialStatement(statements.getStatement(j));
					}
				}
			}
		} else if (sequentialStatement instanceof SequentialExit) {
			logger.info("[Sequence] Exit statement at %d", sequentialStatement.getLocation().fLine);
			checkViolation(sequentialStatement.getLocation()); 
		} else if (sequentialStatement instanceof SequentialIf) {
			SequentialIf ifStatement = (SequentialIf) sequentialStatement;
			logger.info("[Sequence] If statement, condition at %d", ifStatement.getCond().getLocation().fLine);
			checkViolation(ifStatement.getCond().getLocation()); 
			logger.info("[Sequence] If statement, then...");
			SequenceOfStatements thenStatements = ifStatement.getThenStmt();
			if (thenStatements != null) {
				for (int i = 0; i < thenStatements.getNumStatements(); i++) {
					expandSequentialStatement(thenStatements.getStatement(i));
				}
			}
			logger.info("[Sequence] If statement, else...");
			SequenceOfStatements elseStatements = ifStatement.getElseStmt();
			if (elseStatements != null) {
				for (int i = 0; i < elseStatements.getNumStatements(); i++) {
					expandSequentialStatement(elseStatements.getStatement(i));
				}
			}
		} else if (sequentialStatement instanceof SequentialLoop) {
			logger.info("[Sequence] Loop statement at %d", sequentialStatement.getLocation().fLine);
			checkViolation(sequentialStatement.getLocation());
			SequenceOfStatements statements = (SequenceOfStatements) sequentialStatement.getChild(0);
			if (statements != null) {
				for (int j = 0; j < statements.getNumStatements(); j++) {
					expandSequentialStatement(statements.getStatement(j));
				}
			}
		} else if (sequentialStatement instanceof SequentialNextStatement) {
			logger.info("[Sequence] Next statement at %d", sequentialStatement.getLocation().fLine);
			checkViolation(sequentialStatement.getLocation()); 
		} else if (sequentialStatement instanceof SequentialProcedureCall) {
			logger.info("[Sequence] Procedure call statement at %d", sequentialStatement.getLocation().fLine);
			checkViolation(sequentialStatement.getLocation()); 
		} else if (sequentialStatement instanceof SequentialReport) {
			logger.info("[Sequence] Report statement at %d", sequentialStatement.getLocation().fLine);
			checkViolation(sequentialStatement.getLocation()); 
		} else if (sequentialStatement instanceof SequentialSignalAssignment) {
			logger.info("[Sequence] Signal Assignment statement at %d", sequentialStatement.getLocation().fLine);
			checkViolation(sequentialStatement.getLocation()); 
		} else if (sequentialStatement instanceof SequentialVariableAssignment) {
			logger.info("[Sequence] Variable Assignment statement at %d", sequentialStatement.getLocation().fLine);
			checkViolation(sequentialStatement.getLocation()); 
		} else if (sequentialStatement instanceof SequentialWait) {
			logger.info("[Sequence] Wait statement at %d", sequentialStatement.getLocation().fLine);
			checkViolation(sequentialStatement.getLocation()); 
		} else {
			logger.info("[Sequence] Error at %d", sequentialStatement.getLocation().fLine);
			checkViolation(sequentialStatement.getLocation()); 
		}
	}

}
