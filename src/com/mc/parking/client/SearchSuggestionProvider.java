package com.mc.parking.client;

import android.content.SearchRecentSuggestionsProvider;

public class SearchSuggestionProvider extends SearchRecentSuggestionsProvider {
	public final static String AUTHORITY="com.mc.parking.client.search.SearchSuggestionProvider";  
    public final static int MODE=DATABASE_MODE_QUERIES;  
      
    public SearchSuggestionProvider(){  
        super();  
        setupSuggestions(AUTHORITY, MODE);  
    }  
}
