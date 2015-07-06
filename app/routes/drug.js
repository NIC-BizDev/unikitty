import Ember from 'ember';

export default Ember.Route.extend({

  model: function(params) {
    return Ember.RSVP.hash({
      genericName: $.getJSON('https://api.fda.gov/drug/label.json?search=openfda.unii:"' + params.drug_id + '"&count=openfda.generic_name.exact')
        .then(function (data) {

          var max = 0;
          var name = 'Unknown';
          $.each(data.results, function (key, value) {
            if(value.count > max)
            {
              max= value.count;
              name = value.term;
            }
          });
          return name;
        }),
        isOtc: $.getJSON('https://api.fda.gov/drug/label.json?search=openfda.unii:"' + params.drug_id + '"&count=openfda.product_type.exact')
          .then(function (data) {

            var bOtc = false;
            $.each(data.results, function (key, value) {
              bOtc = bOtc || (value.term.toUpperCase().indexOf('OTC') > -1);
            });
            return bOtc;
          }),
      effects: $.getJSON("event?unii=" + params.drug_id),
      recalls: $.getJSON("drug/enforcements?unii=" + params.drug_id),
      interactions: $.getJSON('https://rxnav.nlm.nih.gov/REST/rxcui?idtype=UNII_CODE&id=' + params.drug_id).then(function (data) {

            var rxnormId = data.idGroup.rxnormId[0];

            return $.getJSON('https://rxnav.nlm.nih.gov/REST/interaction/interaction.json?rxcui=' + rxnormId);

        }).then(function (data) {

            var result = {
                rxnormId: data.userInput.rxcui,
                interactions: []
            };

          var toName;
          var toId;
          var PromiseMaker = Ember.ObjectProxy.extend(Ember.PromiseProxyMixin);

          if(data.interactionTypeGroup){
            $.each(data.interactionTypeGroup, function (key, value) {
                $.each(value.interactionType, function (key, value) {
                    $.each(value.interactionPair, function (key, value) {
                        result.interactions.push( {
                            fromDrug: value.interactionConcept[0].sourceConceptItem.name,
                            toDrug: toName = value.interactionConcept[1].sourceConceptItem.name,
                            interaction: value.description,
                            link: value.interactionConcept[0].sourceConceptItem.url,
                            toDrugId: toId = value.interactionConcept[1].minConceptItem.rxcui,
                            toDrugNames: PromiseMaker.create({
                               promise: $.getJSON('https://rxnav.nlm.nih.gov/REST/brands.json?ingredientids=' + toId).then(function (data) {
                                  var brandNames = [];
                                  if(data.brandGroup.conceptProperties)
                                  {
                                   $.each(data.brandGroup.conceptProperties, function (key, value) {
                                      brandNames.push(value.name);
                                   });
                                   }{
                                    brandNames.push(toName);
                                   }

                                   return brandNames;
                              })

                            })
                        });
                    });
                  });
                });
              }
            return result;

        })
    });
  },
  setupController: function(controller, model){
    this._super(controller, model);

    controller.set('model', model);

    Ember.run.schedule('afterRender', this, function () {

		let duration = 1500;

    $(".recall").velocity("transition.slideRightIn", {duration: duration / 2, stagger: 200});
		$('a[data-toggle="tab"]').one('shown.bs.tab', function () {
      $(".effect").velocity("transition.slideRightIn", {duration: duration / 2, stagger: 200});
      $(".interaction").velocity("transition.slideRightIn", {duration: duration / 2, stagger: 200});
		});

		$('.collapse').on('show.bs.collapse', function(){
			$(this).parent().find(".fa-chevron-down").removeClass("fa-chevron-down").addClass("fa-chevron-up");
			$(".panel-body").velocity("fadeIn", {duration: duration});
		}).on('hide.bs.collapse', function(){
			$(this).parent().find(".fa-chevron-up").removeClass("fa-chevron-up").addClass("fa-chevron-down");
		});

		$('[data-toggle="tooltip"]').tooltip();

    });
  }

});
