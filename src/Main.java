import db.DBManager;
import service.Recommender;
import ui.MainUI;
import util.Scheduler;


public class Main {
    public static void main(String[] args) {
        DBManager dbManager = new DBManager();
        Recommender recommender = new Recommender();
        MainUI mainUI = new MainUI(dbManager, recommender);
        Scheduler scheduler = new Scheduler(recommender, dbManager, mainUI);
        scheduler.start();


    }
}
