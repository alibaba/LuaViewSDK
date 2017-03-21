module.exports = {
    book: {
        assets: "./book",
        js: [
        	// 使用hooks - summary:after 取代, 此js仅供参考。
            //"plugin.js"
        ],
        css: [
        	"plugin.css"
        ]
    },
    hooks:{

		"summary:after": function(summaryJson) {

			if(this.options.pluginsConfig && this.options.pluginsConfig['introduction-text']){
				var introText = this.options.pluginsConfig['introduction-text'];
				summaryJson.content.chapters[0].title = introText;
			}
			
			return summaryJson;
		}
    }
};