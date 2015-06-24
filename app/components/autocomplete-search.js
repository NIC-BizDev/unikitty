import Ember from 'ember';

export default Ember.Component.extend({

    classNames: ['autocomplete-search'],
    large: false,
    searchTerm: "what",
    didInsertElement:

    function(){
      $('.drug-search').autocomplete({
        minLength: 3,
        source: function (request, response) {
          $.ajax({
            type: 'GET',
            dataType:'json',
            url: '/autocomplete?name=' + $('.drug-search').val(),
            data: request.value,
            error: function (xhr) {
              console.log('Error: ' + xhr.responseText);
            },
            success: function (data) {
              response($.map(data, function (item) {
                return item;
              }));
            }
          });
        }
      });
    }

});
