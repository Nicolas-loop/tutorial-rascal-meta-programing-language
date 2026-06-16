module Json

import Syntax;
import Parser;
import ParseTree;
import String;
import List;
import IO;

str planningToJson(start[Planning] pt) = planningToJson(pt.top);

str planningToJson((Planning)`<PersonTasks+ people>`)
    = "{\"node\":\"planning\",\"people\":[<intercalate(",", [personToJson(p) | PersonTasks p <- people])>]}";

str personToJson((PersonTasks)`Person <ID name> <Task+ tasks>`)
    = "{\"node\":\"personTasks\",\"name\":\"<name>\",\"tasks\":[<intercalate(",", [taskToJson(t) | Task t <- tasks])>]}";

str taskToJson((Task)`Task <Action action> person <ID name> priority: <INT prio> <Duration? duration>`)
    = "{\"node\":\"task\",\"name\":\"<name>\",\"priority\":<prio>,\"action\":<actionToJson(action)>,\"duration\":[<intercalate(",", [durationToJson(d) | Duration d <- duration])>]}";

str actionToJson((Action)`<LunchAction a>`)   = "{\"node\":\"lunch\",\"action\":<lunchToJson(a)>}";
str actionToJson((Action)`<MeetingAction a>`) = "{\"node\":\"meeting\",\"action\":<meetingToJson(a)>}";
str actionToJson((Action)`<PaperAction a>`)   = "{\"node\":\"paper\",\"action\":<paperToJson(a)>}";
str actionToJson((Action)`<PaymentAction a>`) = "{\"node\":\"payment\",\"action\":<paymentToJson(a)>}";

str lunchToJson((LunchAction)`Lunch <ID place>`)        = "{\"node\":\"lunchAction\",\"location\":\"<place>\"}";
str meetingToJson((MeetingAction)`Meeting <STRING topic>`) = "{\"node\":\"meetingAction\",\"topic\":<topic>}";
str paperToJson((PaperAction)`Report <ID report>`)      = "{\"node\":\"paperAction\",\"report\":\"<report>\"}";
str paymentToJson((PaymentAction)`Pay <INT amount> euro`) = "{\"node\":\"paymentAction\",\"amount\":<amount>}";

str durationToJson((Duration)`duration : <INT amount> <TimeUnit unit>`)
    = "{\"node\":\"duration\",\"amount\":<amount>,\"unit\":\"<trim("<unit>")>\"}";

str jsonFromFile(loc f) = planningToJson(parsePlanning(f));

void main(list[str] args) {
    if (size(args) < 2 || args[0] != "ast") {
        println("{\"error\":\"usage: ast \<file.tdsl\>\"}");
        return;
    }
    println(jsonFromFile(|file://<args[1]>|));
}
