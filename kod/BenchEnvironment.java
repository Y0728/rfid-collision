import java.util.*;
/* The environment which creates the tags and readers and tests them */
public class BenchEnvironment{
    int numberOfTags = 20;
    int numberOfBitsInId = 12;
    int sizeOfId;
    Random randomGenerator = new Random();

    TreeSet<Integer> generatedUniqueIds;
    BSATag[] tags;

    public static void main(String[] args){
        new BenchEnvironment();
    }

    public BenchEnvironment(){
        generatedUniqueIds = new TreeSet<Integer>();
        tags = new BSATag[numberOfTags];

        //Fixing id size from number of bits
        sizeOfId = ((int)Math.pow(2, numberOfBitsInId))-1;
        generateBSATags();

        System.out.println(generatedUniqueIds);

    }


    public void generateBSATags(){
        //Set only takes unique values
        while(generatedUniqueIds.size() < numberOfTags){
            generatedUniqueIds.add(randomGenerator.nextInt(sizeOfId-1)+1);
        }
        int count = 0;
        for(int id : generatedUniqueIds){
            tags[count] = new BSATag(numberOfBitsInId, id);
            count++;
        }
    }
}
