import java.util.*;

/* The environment which creates the tags and readers and tests them */
public class BenchEnvironment{
    int numberOfTags = 100;
    int numberOfBitsInId = 16; // CONDITION: 2^numberOfBitsInId > numberoftags
    int sizeOfId;
    Random randomGenerator = new Random();

    TreeSet<Integer> generatedUniqueIds;
    Tag[] tags;
    BSAReader bsareader;
    DBSAReader dbsareader;
    Reader backtrackreader;
    JumpAndDynamicReader jndreader;
    ImprovedReader impreader;

    public static void main(String[] args){
        new BenchEnvironment();
    }

    public BenchEnvironment(){

        generatedUniqueIds = new TreeSet<Integer>();
        tags = new Tag[numberOfTags];

        //Fixing id size from number of bits
        sizeOfId = ((int)Math.pow(2, numberOfBitsInId))-1;

        generateID();
        generateTags();

        //bsareader = new BSAReader(tags, numberOfBitsInId);
        //bsareader.identifyTags();
        backtrackreader = new ImprovedReader(tags, numberOfBitsInId);
        backtrackreader.identifyTags();
        System.out.println("Avg Queries: " + backtrackreader.getQueryAverage());
        System.out.println("Avg Bits: " + backtrackreader.getBitAverage());

        /*
        numberOfTags = 4;
        tags = new Tag[numberOfTags];
        numberOfBitsInId = 8;
        tags[0] = new Tag(numberOfBitsInId,0b10110100);
        tags[1] = new Tag(numberOfBitsInId,0b10111101);
        tags[2] = new Tag(numberOfBitsInId,0b10101110);
        tags[3] = new Tag(numberOfBitsInId,0b10111110);
        for(int i = 0; i < tags.length; i++){
            System.out.println("Tag " + i + ": " + tags[i]);
        }
        //jndreader = new JumpAndDynamicReader(tags, numberOfBitsInId);
        //jndreader.identifyTags();
        //dbsareader = new DBSAReader(tags, numberOfBitsInId);
        //dbsareader.identifyTags();
        //backtrackreader = new BackTrackReader(tags, numberOfBitsInId);
        //backtrackreader.identifyTags();
        impreader = new ImprovedReader(tags, numberOfBitsInId);
        impreader.identifyTags();*/

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
