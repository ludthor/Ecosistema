package util;

import java.util.Random;

import creatures.Carnivore;
import creatures.Creature;
import creatures.Herbivore;
import creatures.Plant;
import enums.Gender;
import processing.core.PApplet;

public class EcoUtilities {
	
	//Probability takes the chances [0-1] of a creature to be FEMALE
	//And generate the gender according that.
	public static Gender binaryGender(float probability){
		
		float random = (float) Math.random();
		
		if(random <= probability){
			return Gender.FEMALE;
		}else{
			return Gender.MALE;
		}	
	}
	
	public static Gender nonBinaryGender(float probFemale, float probMale){
		
		if(probMale + probFemale > 1){
			return Gender.UNDETERMINED;
		}
		
		float random = (float) Math.random();
		
		if(random <= probFemale){
			return Gender.FEMALE;
		}else if (random > probFemale && random <= (probFemale + probMale) ){
			return Gender.MALE;
		}else{
			return Gender.UNDETERMINED;
		}
	}
	
	public static Creature generateCreature(PApplet p, boolean toroidal,float probC, float probH){
		if(probC + probH > 1){
			return new Plant(p, toroidal);
		}
		
		float random = (float) Math.random();
		
		if(random <= probC){
			return new Carnivore(p, toroidal);
		}else if (random > probC && random <= (probC + probH) ){
			return new Herbivore(p, toroidal);
		}else{
			return new Plant(p, toroidal);
		}
	}
	
	/**
	 * Code adapted from
	 * http://www.java-gaming.org/index.php?topic=35802.0
	 * @return String, containing the random name of the organism.
	 */
	public static String nameGenerator(){
		
		String[] Beginning = { "Kr", "Ca", "Ra", "Mrok", "Cru",
		         "Ray", "Bre", "Zed", "Drak", "Mor", "Jag", "Mer", "Jar", "Mjol",
		         "Zork", "Mad", "Cry", "Zur", "Creo", "Azak", "Azur", "Rei", "Cro",
		         "Mar", "Luk" };
		String[] Middle = { "air", "ir", "mi", "sor", "mee", "clo",
		         "red", "cra", "ark", "arc", "miri", "lori", "cres", "mur", "zer",
		         "marac", "zoir", "slamar", "salmar", "urak" };
		String[] End = { "d", "ed", "ark", "arc", "es", "er", "der",
		         "tron", "med", "ure", "zur", "cred", "mur" };
		   
		Random rand = new Random();


		      return Beginning[rand.nextInt(Beginning.length)] + 
		            Middle[rand.nextInt(Middle.length)]+
		            End[rand.nextInt(End.length)];
		
	}

}
