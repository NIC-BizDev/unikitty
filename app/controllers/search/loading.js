import Ember from 'ember';

export default Ember.Controller.extend({
    needs: 'application',
    searchQuery: Ember.computed.alias('controllers.application.searchQuery')
});
