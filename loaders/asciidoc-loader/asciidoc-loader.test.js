import loader from './index';

it('requires includes and images', () => {
  let loaded = loader(`
    = My Doc
    include::part1.adoc[leveloffset=+1]
    include::part2.adoc[leveloffset=+1]
    image::sunset.jpg[Sunset,300,200]
    image::img/sunrise.jpg[]
  `);

  console.log(loaded);
  expect(loaded).toContain(`require('!file-loader!./part1.adoc')`);
  expect(loaded).toContain(`require('!file-loader!./part2.adoc')`);
  expect(loaded).toContain(`require('!file-loader!./sunset.jpg')`);
  expect(loaded).toContain(`require('!file-loader!./img/sunrise.jpg')`);
});
