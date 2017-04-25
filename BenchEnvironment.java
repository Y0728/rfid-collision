import java.util.*;

/* The environment which creates the tags and readers and tests them */
public class BenchEnvironment{
    int numberOfTags = 10000;
    int numberOfBitsInId = 32; // CONDITION: 2^numberOfBitsInId > numberoftags
    int sizeOfId;
    Random randomGenerator = new Random();

    TreeSet<Integer> generatedUniqueIds;
    Tag[] tags;
    Reader bsareader; //using regular binary search
    Reader dbsareader; //using dynamic binary search
    Reader backtrackreader; //using backtracking algorithm
    Reader jndreader; //using jump and dynamic
    Reader impreader; //using the improved algorithm

    public static void main(String[] args){
        new BenchEnvironment();
    }

    public BenchEnvironment(){

        runTests(50, 6);
        runTests(50, 16);
        runTests(200, 8);
        runTests(200, 10);
        runTests(1000, 16);
        runTests(1000, 32);
        runTests(5000, 16);
        runTests(5000, 32);
        runTests(10000, 16);
        runTests(10000, 32);
        runTests(20000, 16);
        runTests(20000, 32);
        runTests(40000, 16);
        runTests(40000, 32);
        runTests(60000, 16);
        runTests(60000, 32);
        runTests(80000, 32);
        runTests(100000, 32);

    }

    /* Runs all algorithms on this set of tags */
    public void runTests(int numberOfTags, int numberOfBitsInId){
        //Set up the tags
        this.numberOfTags = numberOfTags;
        this.numberOfBitsInId = numberOfBitsInId;
        this.sizeOfId = ((int)Math.pow(2, numberOfBitsInId))-1;
        generatedUniqueIds = new TreeSet<Integer>();
        tags = new Tag[numberOfTags];
        generateID();
        generateTags();

        System.out.println("___________________________________________");
        System.out.println("Test with " + numberOfTags + " tags with " +
                            numberOfBitsInId + " bits in ID");
        System.out.println("Binary Search Algorithm: ");
        bsareader = new BSAReader(tags, numberOfBitsInId);
        bsareader.identifyTags();
        bsareader.calculateResults();
        activateTags();
        System.out.println();

        System.out.println("Dynamic Binary Search Algorithm: ");
        dbsareader = new DBSAReader(tags, numberOfBitsInId);
        dbsareader.identifyTags();
        dbsareader.calculateResults();
        activateTags();
        System.out.println();


        System.out.println("Backtracking Algorithm: ");
        backtrackreader = new BackTrackReader(tags, numberOfBitsInId);
        backtrackreader.identifyTags();
        backtrackreader.calculateResults();
        activateTags();
        System.out.println();


        System.out.println("Jump and Dynamic Algorithm: ");
        jndreader = new JumpAndDynamicReader(tags, numberOfBitsInId);
        jndreader.identifyTags();
        jndreader.calculateResults();
        activateTags();
        System.out.println();


        System.out.println("Improved Algorithm: ");
        impreader = new ImprovedReader(tags, numberOfBitsInId);
        impreader.identifyTags();
        impreader.calculateResults();
        activateTags();
        System.out.println();


    }

    public void activateTags(){
        for(int i = 0; i < tags.length; i++){
            tags[i].activate();
        }
    }

    /* Generate this test's unique ids
     * Random complexity (avoid with large set and many tags)*/
    public void generateID(){
        //TreeSet only takes unique values
        while(generatedUniqueIds.size() < numberOfTags){
            generatedUniqueIds.add(randomGenerator.nextInt(sizeOfId)+1);
        }
    }

    /* Create a Tag for all unique ids */
    public void generateTags(){
        int count = 0;
        for(int id : generatedUniqueIds){
            tags[count] = new Tag(numberOfBitsInId, id);
            count++;
        }
    }
}
