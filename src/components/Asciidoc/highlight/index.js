var hljs = require('./highlight');

hljs.registerLanguage('gradle', require('./languages/gradle'));
hljs.registerLanguage('groovy', require('./languages/groovy'));
hljs.registerLanguage('http', require('./languages/http'));
hljs.registerLanguage('java', require('./languages/java'));
hljs.registerLanguage('xml', require('./languages/xml'));
hljs.registerLanguage('yaml', require('./languages/yaml'));
hljs.registerLanguage('json', require('./languages/json'));

module.exports = hljs;