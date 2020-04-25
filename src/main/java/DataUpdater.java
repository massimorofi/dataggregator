
public class DataUpdater {

	public static void main(String[] args) {
		System.out.println("Aggregating RSS FEEDS ...");
		RSSAggregator.run();
		System.out.println("Aggregating COVID CSV DATA ...");
		COVID_Aggregate.run();
        System.out.println("DATAUPDATER-->END.");
	}

}