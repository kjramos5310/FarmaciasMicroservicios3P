const { createProxyMiddleware } = require('http-proxy-middleware');

module.exports = function(app) {
  // Catalog Service (port 8081)
  app.use(
    '/api/catalog',
    createProxyMiddleware({
      target: 'http://localhost:8081',
      changeOrigin: true,
      pathRewrite: {
        '^/api/catalog': '/api'
      },
      onProxyReq: (proxyReq) => {
        console.log('Proxying catalog request:', proxyReq.path);
      },
      onError: (err, req, res) => {
        console.error('Catalog proxy error:', err.message);
        res.status(500).json({ error: 'Catalog service unavailable' });
      }
    })
  );

  // Inventory Service (port 8082)
  app.use(
    '/api/inventory',
    createProxyMiddleware({
      target: 'http://localhost:8082',
      changeOrigin: true,
      pathRewrite: {
        '^/api/inventory': '/api'
      },
      onProxyReq: (proxyReq) => {
        console.log('Proxying inventory request:', proxyReq.path);
      },
      onError: (err, req, res) => {
        console.error('Inventory proxy error:', err.message);
        res.status(500).json({ error: 'Inventory service unavailable' });
      }
    })
  );

  // Sales Service (port 8083)
  app.use(
    '/api/sales',
    createProxyMiddleware({
      target: 'http://localhost:8083',
      changeOrigin: true,
      pathRewrite: {
        '^/api/sales': '/api'
      },
      onProxyReq: (proxyReq) => {
        console.log('Proxying sales request:', proxyReq.path);
      },
      onError: (err, req, res) => {
        console.error('Sales proxy error:', err.message);
        res.status(500).json({ error: 'Sales service unavailable' });
      }
    })
  );

  // Reporting Service (port 8084)
  app.use(
    '/api/reporting',
    createProxyMiddleware({
      target: 'http://localhost:8084',
      changeOrigin: true,
      pathRewrite: {
        '^/api/reporting': '/api'
      },
      onProxyReq: (proxyReq) => {
        console.log('Proxying reporting request:', proxyReq.path);
      },
      onError: (err, req, res) => {
        console.error('Reporting proxy error:', err.message);
        res.status(500).json({ error: 'Reporting service unavailable' });
      }
    })
  );
};
