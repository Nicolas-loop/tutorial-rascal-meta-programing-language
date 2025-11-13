module Implode

import Syntax;
import Parser;
import AST;

import ParseTree;
import Node;

public Planning implode(Tree pt) = implode(#Planning, pt);
public Planning load(loc l) = implode(#Planning, parsePlanning(l));
