import Ember from 'ember';
//TODO: This Should be handled with a bind-attr style or width on the elements in effet.hbs
export function progressBarPercentage(percent) {
  return ("width: " + percent + "%").htmlSafe();
}

export default Ember.HTMLBars.makeBoundHelper(progressBarPercentage);
