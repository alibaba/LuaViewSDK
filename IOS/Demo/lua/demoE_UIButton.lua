
window.setFrame(0,0,320,480);


function createButton( text , x,y,w,h, callback) {
	local button = UIButton();
	button.setFrame(x,y,w,h);
	button.setText(text);
	button.delegate = ^(){
		print("ok",button);
		if( callback ) {
			callback();
		}
	};
	return button;
}

function test( index ) {
	if( true ) {
		print("index: ", index);
		return "ok";
	}
	return "no";
}

button1 = createButton("测试",10,10,300,60);



button2 = UIButton();
button2.setFrame(10,100+80,300,60);
button2.setText("clear");


button2.delegate = ^(){
	print("clear");
	System.gc();
	collectgarbage("collect");
	local memory = collectgarbage("count");
	print("memory: ", memory );
}