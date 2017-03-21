require(["gitbook"], function(gitbook) {
    
    var pluginConfig;

    var changeText = function(){
    	if(!pluginConfig || !pluginConfig['introduction-text'])return;
		var introText = pluginConfig['introduction-text'] ? pluginConfig['introduction-text'] : 'Introduction';
        $('.summary li[data-level="0"] a').html(introText);
        document.title = document.title.replace('Introduction',introText);
    };

    gitbook.events.bind("start", function(e, config) {
    	pluginConfig = config;
    	changeText();
    });

    gitbook.events.bind("page.change", function() {
    	changeText();
    });

    gitbook.events.bind("exercise.submit", function(e, data) {
        
    });
});