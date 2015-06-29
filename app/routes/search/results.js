import Ember from 'ember';

export default Ember.Route.extend({
    model: function(params) {
        return $.getJSON("/drug?name=" + params.keyword);
    },
    setupController: function(controller, model){
        this._super(controller, model);

       Ember.run.schedule('afterRender', this, function () {

         let duration = 1500;
         // Animations
         $(".drug").velocity("transition.slideRightIn", {duration: duration, stagger: 150});
       });
     }
});
