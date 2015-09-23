
window.setFrame(0,0,320,480);

if( a != "error" ) {
	print("!= error");
}


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

if( a != "error" ) {
	print("!= error");
}

if( a == "ok" ) {
	print("== ok ");
}

i = 0;
while(i<10 ) {
	print("while i = ", i);
	i = i+1;
}

while ( i<10 )  {
	print("while oneline i = ", i+1 );
}

function getNewIndex( index ) {
    print( "getNewIndex: " , index );
	index = index +1;
	return index;
}

i=3;

while  (i<10)
	i = getNewIndex(i);


t = {"value1","value2",a="avalue", b="bvalue"};

for ( key,value,temp in pairs(t) ) {
    print("for in: ", key,"  ",t[key],"---",value,"---", temp);
}


for ( i=0,10,1 ){
	print("for i = ", i );
}




for ( i=0; 10; 1 ){
	{
		print("block test ", i );
	}
}


if( a == "ok1" )
	print("== ok1 ");
else if( a=="ok")
    print("== ok ");

if( a )
  print("yes");
else
  print("no");


	button1 = nil;
	
	System.gc();
	collectgarbage("collect");


	memory = collectgarbage("count");
	print("memory: ", memory );
	
