import React from 'react';

export default function Footer() {
  return (
    <div className="bg-inverse text-center">
      <div className="col-12" style={{padding: 10, color: 'white'}}>
        <p>© 2017-2021 Micrometer Application Monitoring, powered by <a href="http://pivotal.io">Pivotal</a> & <a href="http://spring.io">Spring</a></p>
      </div>
    </div>
  )
}