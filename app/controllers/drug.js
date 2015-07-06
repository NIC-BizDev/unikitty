import Ember from 'ember';

export default Ember.Controller.extend({
    needs: 'application',
    // Current User Data
    UserData: Ember.computed.alias('controllers.application.content'),
    // Current Drug
    drugName: Ember.computed.alias('controllers.application.currentDrugName'),

    // Search Drug Interactions
    interactionSearchQuery: null,
    //interactionSearchQueryRegEx: new RegExp(interactionSearchQuery, 'i'),
    interactionSearchResults: function() {
        // Get the search term
        var searchTerm = this.get('interactionSearchQuery');

        // return out of function if blank
        if (!searchTerm) {return;}

        // Create a RegEx that ignores case
        var regex = new RegExp(searchTerm, 'i');

        // Return the results
        return this.model.interactions.interactions.filter(function(interaction){
            var bMatch = false;
            if(interaction.toDrug.match(regex))
            {
                bMatch = true;
            }
            else{

                $.each(interaction.toDrugNames.content, function (index, value) {
                    if(value.match(regex))
                    {
                        bMatch = true;
                        return false;
                    }
                });
            }
            return bMatch;
        });
    }.property('interactionSearchQuery'),

    // Search Adverse Effects
    effectSearchQuery: null,
    effectSearchResults: function() {
        // Get the search term
        var searchTerm = this.get('effectSearchQuery');

        // return out of function if blank
        if (!searchTerm) {return;}

        // Create a RegEx that ignores case
        var regex = new RegExp(searchTerm, 'i');

        // Return the results
        return this.model.effects.effect.filter(function(effect){
            return effect.effect.match(regex);
        });
    }.property('effectSearchQuery')
});
