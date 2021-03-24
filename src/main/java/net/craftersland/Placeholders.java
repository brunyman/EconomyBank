package net.craftersland;

import java.text.DecimalFormat;

import org.bukkit.entity.Player;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;

public class Placeholders extends PlaceholderExpansion {
	
	Money pl;
	
	public Placeholders(Money plugin) {
		this.pl = plugin;
	}
	
	@Override
    public boolean persist(){
        return true;
    }
	
	@Override
    public boolean canRegister(){
        return true;
    }

	@Override
    public String getAuthor(){
        return pl.getDescription().getAuthors().toString();
    }

	/**
     * The placeholder identifier should go here.
     * <br>This is what tells PlaceholderAPI to call our onRequest 
     * method to obtain a value if a placeholder starts with our 
     * identifier.
     * <br>This must be unique and can not contain % or _
     *
     * @return The identifier in {@code %<identifier>_<value>%} as String.
     */
    @Override
    public String getIdentifier(){
        return "economybank";
    }

    @Override
    public String getVersion(){
        return pl.getDescription().getVersion();
    }
    
    /**
     * This is the method called when a placeholder with our identifier 
     * is found and needs a value.
     * <br>We specify the value identifier in this method.
     * <br>Since version 2.9.1 can you use OfflinePlayers in your requests.
     *
     * @param  player A Player
     * @param  identifier
     *         A String containing the identifier/value.
     *
     * @return possibly-null String of the requested identifier.
     */
    @Override
    public String onPlaceholderRequest(Player player, String identifier){
        if (player == null) {
            return "Not online!";
        }

        Double bal = pl.getMoneyDatabaseInterface().getBalance(player);

        // %economybank_blahblah%
        // Returns the placeholder formatted with commas
        if (identifier.equals("balance_formatted")) {
        	DecimalFormat f = new DecimalFormat("#,##0.00");
			
			if (bal.toString().endsWith(".0")) {
				f = new DecimalFormat("#,##0");
			}
            return f.format(bal);
        }

        // Returns the placeholder without commas
        if (identifier.equals("balance")) {
            DecimalFormat f = new DecimalFormat("###0.00");

            if (bal.toString().endsWith(".0")) {
                f = new DecimalFormat("###0");
            }
            return f.format(bal);
        }

        // Returns the placeholder floored (rounded down) as an integer
        if (identifier.equals("balance_fixed")) {
            return String.valueOf((int) Math.floor(bal));
        }
 
        // We return null if an invalid placeholder (f.e. %someplugin_placeholder3%) 
        // was provided
        return null;
    }

}
