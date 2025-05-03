import java.io.IOException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.Scanner;
import java.io.File;
import java.io.FileWriter;
import java.io.FileNotFoundException;

public class MarvelCharacterReadingOrder {
    public static void getCharacters(){
        long startTime = System.currentTimeMillis();
        try {
            Document characterSite;
            Elements divs;
            boolean hasNext = true;
            boolean firstGo = true;
            String nextUrl = "https://marvel.fandom.com/wiki/Category:Characters";
            String charURL = "";
            do{
                characterSite = Jsoup.connect(nextUrl).get(); //Connects to next character page
                divs = characterSite.select("li[class=category-page__member]");

                for (Element div : divs){ //Steals all the character urls
                    if(String.valueOf(div).contains("Category:") || String.valueOf(div).contains("Character")){
                        continue;
                    }
                    charInfo(div.getElementsByClass("category-page__member-link").attr("href")); //Calls function to steal character info
                }

                Elements hasNextElement = characterSite.select("div[class=category-page__pagination]");  //Checks if last page
                if (hasNextElement.text().contains("Next")){
                    nextUrl = hasNextElement.select("a[class=category-page__pagination-next wds-button wds-is-secondary]").attr("href"); //Steals next character list page
                } else{
                    hasNext = false;
                }
                firstGo = false;
            }while(hasNext);

            long endTime = (System.currentTimeMillis() - startTime); //Tracks runtime
            double endTimeinSec = (double)endTime/1000;
            System.out.println("TOTAL RUNTIME: " +  endTimeinSec + " seconds");
        } catch (IOException e) {
            System.out.println("Error caught");
            throw new RuntimeException(e);
        }
    }

    public static void charInfo(String charURL){ //Inputs last part of URL, "/wiki/Peter_Parker_(Earth-616)"
        try {
            System.out.println(charURL);
            Document character = Jsoup.connect("https://marvel.fandom.com" + charURL).get();
            FileWriter charInfo = new FileWriter("charInfo.csv", true); //FIXED: Make new excel, import data with UTF-8
            Elements characterNames;
            int numAppearances = 0;
            charInfo.write(character.select("span[class=mw-page-title-main]").text() + "\t");
            charInfo.write("|PRIO|Title\t");
            if (character.select("div[class=pi-item pi-data pi-item-spacing pi-border-color]").get(0).attr("data-source").equals("Name")){ //If first category is "name"
                characterNames = character.select("div[class=pi-item pi-data pi-item-spacing pi-border-color]").get(0).select("div[class=pi-data-value pi-font]");
                if (String.valueOf(characterNames).contains("<sup")){ //Removes the [1] from characers with references
                    charInfo.write(characterNames.get(0).child(0).text() + "\t");
                } else{
                    charInfo.write(characterNames.text() + "\t");
                }
            }
            charInfo.write("|PRIO|Name\t");

            if (character.select("div[class=pi-item pi-data pi-item-spacing pi-border-color]").get(1).attr("data-source").equals("CurrentAlias")){ //If second category is "CurrentAlias"
                characterNames = character.select("div[class=pi-item pi-data pi-item-spacing pi-border-color]").get(1).select("div[class=pi-data-value pi-font]");
                if (String.valueOf(characterNames).contains("<sup")){ //Removes the [1] from characers with references
                    charInfo.write(characterNames.get(0).child(0).text() + "\t");
                } else{
                    charInfo.write(characterNames.text() + "\t");
                }
            }
            charInfo.write("|PRIO|CurrentAlias\t");

 /*       if (character.select("aside[class=portable-infobox pi-background pi-border-color pi-theme-character pi-layout-default]").text().contains("Codenames:")){ //Checks if there is a codesnames section
                System.out.println("WE FOUND CODENAMES IN " + charURL);
                if(character.select("aside[class=portable-infobox pi-background pi-border-color pi-theme-character pi-layout-default]").toString().contains("class=\"navigation\"")){
                    System.out.println("THERES ALSO A NAVIGATION PANEL IN " + charURL); //Triggers if there is any nav panel at all
                } else{
                    Elements divs = character.select("aside[class=portable-infobox pi-background pi-border-color pi-theme-character pi-layout-default]").select("div[class=pi-data-value pi-font]");
                    System.out.println(divs);
                }



            }
            charInfo.write("\t|PRIO|Codenames\t");
*/
            charInfo.write("|PRIO|Appearances\t");
            characterNames = character.select("span[id=See_Also]").getFirst().parent().nextElementSibling().select("li");
            for (Element div : characterNames){
                if (div.text().contains(" minor appearance(s) of ")){
                    numAppearances = numAppearances + Integer.parseInt(div.text().split(" minor appearance")[0]);
                } else if (div.text().contains(" appearance(s) of ")){
                    numAppearances = numAppearances +  Integer.parseInt(div.text().split(" appearance")[0]);
                }
            }
            charInfo.write(String.valueOf(numAppearances) + "\t");

            charInfo.write("[URL]\t");
            charInfo.write("https://marvel.fandom.com" + charURL + "\n");
            charInfo.close();
        } catch (IOException e) {
            System.out.println("Error while writing to file");
            throw new RuntimeException(e);
        }
    }

    public static void searchbar(String search){
        File charListFile = new File("MarvelCharactersV1UnfinishedTXT.txt");
        String character;
        String[] charArray;
        String[][] topResults = new String[5][3]; //topResults is a 2d array with [[Name], [NumAppearances], [URL]]
        for (int i = 0; i < topResults.length; i++){
            for (int j = 0; j < topResults[i].length; j++){
                topResults[i][j] = "-1";
            }
        }
        boolean resultFound = false;
        String currentChar = "", currentNumAppearances = "", currentURL = "";
        int currentPlacement = 0;

        try{
            Scanner charList = new Scanner(charListFile);
            while(charList.hasNext()){

                character = charList.nextLine();
                charArray = character.split("\t");
                currentChar = charArray[0];
                for (int i = 0; i < charArray.length; i++){
                    if (charArray[i].contains(search)){
                        resultFound = true;
                    }
                    if (resultFound && charArray[i].equals("|PRIO|Appearances")){
                        currentNumAppearances = charArray[i+1];
                    }
                    if (resultFound && charArray[i].equals("[URL]")){
                        currentURL = charArray[i+1];
                    }
                }

                if (resultFound){
                }
                resultFound = false;
            }
        } catch(FileNotFoundException e){
            System.out.println("FILE NOT FOUND");
        }
    }

    public static void main(String[] args) {
        Scanner scan = new Scanner(System.in);
        System.out.println("Do you have the list of all characters already? \nIf not, enter 'no' (Warning: This will take a really long time and the file is provided)");
        //getCharacters();
        searchbar("Bruce Banner");
        //if (scan.next().equals("no")){
        //charInfo("/wiki/Clinton_Barton_(Earth-616)");
        //charInfo("/wiki/Aña_Corazón_(Earth-616)");
        //}
        //searchbar("Spider-Man");
    }
}
