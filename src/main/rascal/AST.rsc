module AST

data Planning = planning(set[PersonTasks] personList);
data PersonTasks = personTasks(str name, list[Task] tasks);
data Task = task(Action action, int prio, list[Duration] duration);
data Duration = duration(int dl, TimeUnit unit);
data Action = lunch(LunchAction lunchAction) 
            | meeting(MeetingAction meetingAction)
            | paper(PaperAction paperAction) 
            | payment(PaymentAction paymentAction);
data LunchAction = lunchAction(str location);
data MeetingAction = meetingAction(str topic);
data PaperAction = paperAction(str report);
data PaymentAction = paymentAction(int amount);
data TimeUnit = minute(Minute minute)
              | hour(Hour hour)
              | day(Day day)
              | week(Week week);
data Minute = minute();
data Hour = hour();
data Day = day();
data Week = week();
