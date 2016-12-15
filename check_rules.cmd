rem This script is aimed at launching the Rulechecker from a command-line interface.
rem The Zamiacad Plugin must be installed in the Eclipse IDE.

set ECLIPSE_HOME=D:/CNES/eclipse-luna/eclipsec.exe
set PROJECT=D:/CNES/zamia-dev/RULE_CHECKER/trunk/_tests/TestRuleCheck/Top
%ECLIPSE_HOME% -nosplash -application org.zamia.plugin.Check %PROJECT%
