module Parser

import Syntax;
import ParseTree;

public start[Planning] parsePlanning(str src, loc origin) = parse(#start[Planning], src, origin);
public start[Planning] parsePlanning(loc origin) = parse(#start[Planning], origin);
