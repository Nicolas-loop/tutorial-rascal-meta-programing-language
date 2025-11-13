module Plugin

import IO;
import ParseTree;
import util::Reflective;
import util::IDEServices;
import util::LanguageServer;
import Relation;

import Syntax;
import Generator1;

PathConfig pcfg = getProjectPathConfig(|project://tutorial-rascal-meta-programing-language|);
Language tdslLang = language(pcfg, "TDSL", "tdsl", "Plugin", "contribs");

data Command = gen1(Planning p);

set[LanguageService] contribs() = {
    parser(start[Planning] (str program, loc src) {
        return parse(#start[Planning], program, src);
    }),
    lenses(rel[loc src, Command lens] (start[Planning] p) {
        return {
            <p.src, gen1(p.top, title="Generate text file")>
        };
    }),
    executor(exec)
};

value exec(gen1(Planning p)) {
    rVal = generator1(p);
    outputFile = |project://tutorial-rascal-meta-programing-language/src/main/rascal/instance/output/generator1.txt|; 
    writeFile(outputFile, rVal);
    edit(outputFile);
    return ("result": true);
}

void main() {
    registerLanguage(tdslLang);
}
