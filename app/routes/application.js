import Ember from 'ember';

export default Ember.Route.extend({
    actions: {
        performSearch: function(keyword) {
            var controller = this.get("controller");
            this.refresh();
            controller.set("searchQuery", keyword.toUpperCase().replace(/\+/g," "));
            this.transitionTo('search.results', keyword);
        },
        setCurrentDrug: function(drugName, drugId) {
            // Set the current drug on the application
            var controller = this.get("controller");
            controller.set("currentDrugName", drugName);

            //Transition to the drug detail page
            this.transitionTo('drug', drugId);
        },
        error: function() {
            this.transitionTo('index');
        }
    },

    setupController: function(controller, model){
       this._super(controller, model);
       Ember.run.schedule('afterRender', this, function () {
         let duration = 1500;
         // Animations
         $(".recall").velocity("transition.slideRightIn", {duration: duration, stagger: 150});
       });
   }

});
