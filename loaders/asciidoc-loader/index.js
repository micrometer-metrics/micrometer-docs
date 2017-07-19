// Works similarly to html-loader to cause React to pack adoc includes into the distribution
// and replacing the include link with the packed file.

module.exports = function(content) {
  return "module.exports = " + JSON.stringify(content)
    .split('\\n')
    .map(line => line.replace(/(image|include)::([^.]+).([^\[]+)\[(.*)\]/g,
      (_, type, name, ext, importProps) => `${type}::" + require('!file-loader!./${name}.${ext}') + "[${importProps}]`))
    .join('\\n');
};
