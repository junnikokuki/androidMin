if (!window.__res_root && !window.__app_root) {
  window.__res_root = './resource/';
  window.__app_root = './';
}

const loadScript = function (list, callback) {
  var loaded = 0;
  const loadNext = function () {
    loadSingleScript(list[loaded], function () {
      loaded++;
      if (loaded >= list.length) {
        callback();
      } else {
        loadNext();
      }
    });
  };
  loadNext();
};

const loadSingleScript = function (src, callback) {
  const s = document.createElement('script');
  s.async = false;
  s.src = window.__app_root + src;
  s.addEventListener('load', function () {
    if (s.parentNode) {
      s.parentNode.removeChild(s);
    }
    // s.removeEventListener('load', arguments.callee as any, false);
    callback();
  }, false);
  document.body.appendChild(s);
};

const xhr = new XMLHttpRequest();
const manifestPrefix = window.__res_root === 'resource/' ? './' : window.__res_root.replace('resource/');
xhr.open('GET', window.__app_root + 'manifest.json?v=' + Math.random(), true);
xhr.addEventListener('load', function () {
  const manifest = JSON.parse(xhr.response);
  const list = manifest.initial.concat(manifest.game);
  loadScript(list, function () {
    /**
     * {
     * "renderMode":, //Engine rendering mode, "canvas" or "webgl"
     * "audioType": 0 //Use the audio type, 0: default, 2: web audio, 3: audio
     * "antialias": //Whether the anti-aliasing is enabled in WebGL mode, true: on, false: off, defaults to false
     * "calculateCanvasScaleFactor": //a function return canvas scale factor
     * }
     **/
    window.egret.runEgret({
      renderMode: 'webgl', audioType: 0, calculateCanvasScaleFactor: function (context) {
        const backingStore = context.backingStorePixelRatio ||
          context.webkitBackingStorePixelRatio ||
          context.mozBackingStorePixelRatio ||
          context.msBackingStorePixelRatio ||
          context.oBackingStorePixelRatio ||
          context.backingStorePixelRatio || 1;
        return (window.devicePixelRatio || 1) / backingStore;
      }
    });
  });
});
xhr.send(null);
