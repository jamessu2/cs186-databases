package edu.berkeley.MetadataRepo;

import java.util.Scanner;

/**
 * Main program
 */
public class Main
{
    public static void main(String[] args)
    {
        MetadataRepo repo = new MetadataRepo("54.69.1.154");
        System.out.println("Welcome to the Metadata Repo shell!");

        try {Thread.sleep(500);} catch (InterruptedException e) {e.printStackTrace();}

        System.out.print("> ");
        Scanner scan = new Scanner(System.in);
        while (scan.hasNext())
        {
            repo.execute(scan.nextLine());

//            BasicDBObject parseResult = Parser.parseExpression(scan.nextLine());
//            if (parseResult != null)
//                System.out.println(parseResult.toString());

            System.out.print("> ");
        }
    }
}
