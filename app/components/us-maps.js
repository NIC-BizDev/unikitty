import Ember from 'ember';

export default Ember.Component.extend({

	distribution_pattern: [],

	_initializeMaps: function() {

		var states = this.get('distribution_pattern'),
		    stateObj = {},
		    fillStyle;

		if (states.length > 0) {
			$.each(states, function(index, value){
				stateObj[value] = {fill: '#3366cc'};
			});
			fillStyle = "#eee";
		} else {
			// If states is blank this means highlight all states
			fillStyle = "#3366cc";
		}

		this.$().usmap({
			showLabels: false,
			stateStyles: {
				fill: fillStyle,
				stroke: '#fff'
			},
			stateHoverStyles: {fill: '#cccccc'},
			stateSpecificStyles: stateObj
		});

	}.on('didInsertElement')

});
