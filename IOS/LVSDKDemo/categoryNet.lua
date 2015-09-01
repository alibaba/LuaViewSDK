
options = {};

mtopOptionsApiCallback = nil;

function requestOptionListInfo( )
	showOptionLoadingView();
	Mtop( 
		  {
				method = "mtop.ju.block.option.get",
				version= "1.0",
				params = {
							platformId = "8001",
							page = 1,
							pageSize = 10000,
						 },
		  },
		  ^( data, error){
		  		if ( data ) {
			  		options = data.model;
			  		-- 注册属性和方法列表
			  		for( i=1; table:getn(options); 1) {
			  			local  option = options[i];
			  			setOptionMethod( option, i);
			  			print(option);
			  		}
			  		-- 处理options 回调
			  		if( mtopOptionsApiCallback ) {
			  			mtopOptionsApiCallback();
			  		}
		  		} else {
		  			print(error);
		  			showOptionErrorView();
		  		}
		  }
	);
end



function  setOptionMethod( option, i )
	option.index = i;
	option.isLoading = false;
	option.haveItems = false;

	function option.requestItems(callback)
		if( self.isLoading ) {
			return;
		} 
		self.isLoading = true;
		print(self.displayName, self.optStr);
		self.showLoadingView();
		self.hiddenErrorView();
		Mtop( 
			  {
					method = "mtop.ju.block.optionminisite.get",
					version= "1.0",
					params = {
								platformId = "8001",
								page = 1,
								pageSize = 10000,
								optStr = self.optStr
							 },
			  },
			  ^( data, error){
			  		self.isLoading = false;
			  		if( data ) {
				  		local model = data.model;
				  		if( self.extend==nil )
				  			self.extend = {};
				  		self.extend.syncOutput = model;
				  		self.haveItems = true;
						print(self.displayName, self.optStr, "loaded: ");
			  		} else {
						print(self.displayName, self.optStr, "error: ", error);
			  		}
			  		if( callback ) {
			  			callback(option);
			  		}
					self.hiddenLoadingView();
			  }
		);
	end

	function option.requestIfNoItems( callback )
		if ( self.haveItems ) {
			return ;
		} else {
			self.requestItems(callback);
		}
	end

end





