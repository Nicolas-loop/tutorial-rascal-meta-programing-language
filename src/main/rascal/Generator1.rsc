module Generator1

import IO;
import Set;
import List;
import AST;
import Syntax;
import String;

import Parser;
import Implode;

data GenTask = genTask(str action = "", int prio = 0, str duration = "");
list[str] allPersons = []; // normally do not do this
list[GenTask] allPlans = [];

void main() {
    cast = parsePlanning(|project://tutorial-rascal-meta-programing-language/src/main/rascal/instance/spec1.tdsl|);
    rVal = generator1(cast);
    println(rVal);
    writeFile(|project://tutorial-rascal-meta-programing-language/src/main/rascal/instance/output/generator1.txt|, rVal);
}

str generator1(cast) {
    ast = implode(cast);
    allPersons = []; // init to empty for the case you want to generate a second file
    allPlans = [];
    generate(ast);
    rVal = 
        "Info of the planning DepartmentABC
        'All Persons:
	    '       <for (person <- allPersons) {><person>
        '       <}>
        'All actions of tasks:
        '======
        '       <intercalate(" &\n", 
                [ "<plan.action> <plan.prio> <plan.duration>" | plan <- allPlans])>
        '=====
        'Other way of listing all tasks:
        '       <intercalate(" ,\n", [ "<plan.action> <plan.prio>" | plan <- allPlans])>
        '";
    return rVal;
}

void generate(Planning planning) {
    for (personTask <- planning.personList) {
        generate(personTask);
    }
}

void generate(PersonTasks personTasks) {
    allPersons += "<personTasks.name>";
    for (task <- personTasks.tasks) {
        generate(task);
    }
}

void generate(Task task) {
    rVal = genTask(action = generateAction(task.action)); // via constructor and via fields see next
    rVal.prio = toInt("<task.prio>");
    for (dur <- task.duration) {
        rVal.duration = generateDuration(dur);
    }
    allPlans += rVal;
}

str generateAction(Action action) {
    if (action.lunchAction?)   return generateAction(action.lunchAction);
    if (action.meetingAction?) return generateAction(action.meetingAction);
    if (action.paperAction?)   return generateAction(action.paperAction);
    if (action.paymentAction?) return generateAction(action.paymentAction);
    return "Unknown action!";
}

str generateAction(LunchAction lunchAction) {
    return "Lunch at location <lunchAction.location>";
}

str generateAction(MeetingAction meetingAction) {
    return "Meeting with topic <replaceAll(meetingAction.topic, "\"", "")>";
}

str generateAction(PaperAction paperAction) {
    return "Paper for journal <paperAction.report>";
}

str generateAction(PaymentAction paymentAction) {
    return "Pay <paymentAction.amount> Euro";
}

str generateDuration(Duration dur) {
    return "with duration: <dur.dl> <generateDuration(dur.unit)>";
}

str generateDuration(TimeUnit timeUnit) {
    if (timeUnit.minute?) return "m";
    if (timeUnit.hour?)   return "h";
    if (timeUnit.day?)    return "d";
    if (timeUnit.week?)   return "w";
    return "Unknow time unit";
}
