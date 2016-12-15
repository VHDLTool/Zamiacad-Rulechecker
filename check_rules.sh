#!/bin/sh

# This script is aimed at launching the Rulechecker from a command-line interface.
# The Zamiacad Plugin must be installed in the Eclipse IDE.

ECLIPSE_HOME=/opt/eclipse/eclipse
PROJECT=HOME/CNES/zamia-dev/RULE_CHECKER/trunk/_tests/TestRuleCheck/Top

$ECLIPSE_HOME -nosplash -application org.zamia.plugin.Check $PROJECT

