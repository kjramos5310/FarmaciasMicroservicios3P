const { createProxyMiddleware } = require('http-proxy-middleware');

module.exports = function(app) {
  // Todas las rutas /api/** ahora van al Gateway (puerto 8123)
  // El Gateway se encarga de enrutar a los microservicios correctos
  app.use(
    '/api',
    createProxyMiddleware({
      target: 'http://localhost:8123',
      changeOrigin: true,
      // NO hacemos pathRewrite - dejamos que el Gateway maneje las rutas completas
      onProxyReq: (proxyReq) => {
        console.log('Proxying request to Gateway:', proxyReq.path);
      },
      onError: (err, req, res) => {
        console.error('Gateway proxy error:', err.message);
        res.status(500).json({ error: 'Gateway unavailable' });
      }
    })
  );
};
