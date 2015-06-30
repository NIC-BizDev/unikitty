import Ember from 'ember';

export function progressBarPercentage(percent) {
  return ("width: " + percent + "%").htmlSafe();
}

export default Ember.HTMLBars.makeBoundHelper(progressBarPercentage);
