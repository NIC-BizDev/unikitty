import Ember from 'ember';

export default Ember.Controller.extend({
    needs: 'application',
    drugName: Ember.computed.alias('controllers.application.currentDrugName')
});
