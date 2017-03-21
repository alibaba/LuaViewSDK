var tester = require('gitbook-tester');
var test = require('tape');

var pkg = require('./package.json');

test('highlight javascript code block', function (t) {

  t.plan(1);

  tester.builder()
    .withContent('```js\nfunction() { return true };\n```')
    .withLocalPlugin(__dirname)
    .withBookJson({
      gitbook: pkg.engines.gitbook,
      plugins: ['prism', '-highlight']
    })
    .create()
    .then(function(result) {
      var expected = '<pre class="language-"><code class="lang-js"><span class="token keyword">function</span><span class="token punctuation">(</span><span class="token punctuation">)</span> <span class="token punctuation">{</span> <span class="token keyword">return</span> <span class="token boolean">true</span> <span class="token punctuation">}</span><span class="token punctuation">;</span></code></pre>';
      t.equal(result[0].content, expected);
    })
    .done();

});

test('highlight csharp code using shortcut', function (t) {

  t.plan(1);

  tester.builder()
    .withContent('```cs\nusing System; class Program {public static void Main(string[] args) {Console.WriteLine("Hello, world!"); } }\n```')
    .withLocalPlugin(__dirname)
    .withBookJson({
      gitbook: pkg.engines.gitbook,
      plugins: ['prism', '-highlight']
    })
    .create()
    .then(function(result) {
      var expected = '<pre class="language-"><code class="lang-cs"><span class="token keyword">using</span> System<span class="token punctuation">;</span> <span class="token keyword">class</span> <span class="token class-name">Program</span> <span class="token punctuation">{</span><span class="token keyword">public</span> <span class="token keyword">static</span> <span class="token keyword">void</span> <span class="token function">Main</span><span class="token punctuation">(</span><span class="token keyword">string</span><span class="token punctuation">[</span><span class="token punctuation">]</span> args<span class="token punctuation">)</span> <span class="token punctuation">{</span>Console<span class="token punctuation">.</span><span class="token function">WriteLine</span><span class="token punctuation">(</span><span class="token string">&quot;Hello, world!&quot;</span><span class="token punctuation">)</span><span class="token punctuation">;</span> <span class="token punctuation">}</span> <span class="token punctuation">}</span></code></pre>';
      t.equal(result[0].content, expected);
    })
    .done();

});
