import Ember from 'ember';

export default Ember.Component.extend({
  drugId: "",
  name: "",
  count: "",
  description: "",
  percent: 0,
  malePercent: 0,
  femalePercent: 0,
  ages: [],
  needs: ['application'],
  // These classes allow bootstrap to declare only one open panel at a time
  classNames: ['effect panel panel-default'],
  _initializeAccordion: function() {
  }.on('didInsertElement')

});
