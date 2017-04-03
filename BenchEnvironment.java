import java.util.*;

/* The environment which creates the tags and readers and tests them */
public class BenchEnvironment{
    int numberOfTags = 20000;
    int numberOfBitsInId = 32; // CONDITION: 2^numberOfBitsInId > numberoftags
    int sizeOfId;
    Random randomGenerator = new Random();

    TreeSet<Integer> generatedUniqueIds;
    BSATag[] tags;
    BSAReader bsareader;

    public static void main(String[] args){
        new BenchEnvironment();
    }

    public BenchEnvironment(){

        generatedUniqueIds = new TreeSet<Integer>();
        tags = new BSATag[numberOfTags];

        //Fixing id size from number of bits
        sizeOfId = ((int)Math.pow(2, numberOfBitsInId))-1;

        generateID();
        generateBSATags();
        bsareader = new BSAReader(tags, numberOfBitsInId);
        bsareader.identifyTags();


        /*
        //DEBUG
        for(int i = 0; i < tags.length; i++){
            char[] s = tags[i].respondBSAQuery("110000".toCharArray());
            if(s != null){
                System.out.println(s);
            }
        }*/



        /*
        numberOfTags = 4;
        tags = new BSATag[numberOfTags];
        numberOfBitsInId = 4;
        tags[0] = new BSATag(numberOfBitsInId,0b0111);
        tags[1] = new BSATag(numberOfBitsInId,0b1011);
        tags[2] = new BSATag(numberOfBitsInId,0b1110);
        tags[3] = new BSATag(numberOfBitsInId,0b0110);
        bsareader = new BSAReader(tags, numberOfBitsInId);
        bsareader.identifyTags();
        */

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
