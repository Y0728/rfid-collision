import java.util.*;

/* The environment which creates the tags and readers and tests them */
public class BenchEnvironment{
    int numberOfTags = 100;
    int numberOfBitsInId = 16; // CONDITION: 2^numberOfBitsInId > numberoftags
    int sizeOfId;
    Random randomGenerator = new Random();

    TreeSet<Integer> generatedUniqueIds;
    BSATag[] tags;
    BSAReader bsareader;

    public static void main(String[] args){
        new BenchEnvironment();
    }

    public BenchEnvironment(){

//        generatedUniqueIds = new TreeSet<Integer>();
//        tags = new BSATag[numberOfTags];
//
//        //Fixing id size from number of bits
//        sizeOfId = ((int)Math.pow(2, numberOfBitsInId))-1;
//
//        generateID();
//        generateBSATags();
//
//        bsareader = new BSAReader(tags, numberOfBitsInId);
//        bsareader.identifyTags();
//        System.out.println("Avg Queries: " + bsareader.getQueryAverage());
//        System.out.println("Avg Bits: " + bsareader.getBitAverage());



        numberOfTags = 4;
        tags = new BSATag[numberOfTags];
        numberOfBitsInId = 8;
        tags[0] = new BSATag(numberOfBitsInId,0b10100111);
        tags[1] = new BSATag(numberOfBitsInId,0b10110101);
        tags[2] = new BSATag(numberOfBitsInId,0b10101111);
        tags[3] = new BSATag(numberOfBitsInId,0b10111101);
        for(int i = 0; i < tags.length; i++){
            System.out.println("Tag " + i + ": " + tags[i]);
        }
        bsareader = new BSAReader(tags, numberOfBitsInId);
        bsareader.identifyTags();


    }

    /* Generate this test's unique ids
     * Random complexity (avoid with large set and many tags)*/
    public void generateID(){
        //TreeSet only takes unique values
        while(generatedUniqueIds.size() < numberOfTags){
            generatedUniqueIds.add(randomGenerator.nextInt(sizeOfId)+1);
        }
    }

    /* Create a BSAtag for all unique ids */
    public void generateBSATags(){
        int count = 0;
        for(int id : generatedUniqueIds){
            tags[count] = new BSATag(numberOfBitsInId, id);
            count++;
        }
    }
}
