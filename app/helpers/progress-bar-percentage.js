import Ember from 'ember';

export function progressBarPercentage(percent) {
  return "width: " + percent + "%";
}

export default Ember.HTMLBars.makeBoundHelper(progressBarPercentage);
