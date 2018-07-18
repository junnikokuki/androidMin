(function(){
    var codeEditor = window.codeEditor = window.codeEditor || {};
    const gameCore = `
    var $builtinmodule = function (name) {
        var mod = {};

        mod.moveDirection = new Sk.builtin.func(function (direction, step) {
            Sk.builtin.pyCheckArgs("moveDirection", arguments, 2, 2);
            Sk.builtin.pyCheckType("direction", "string", Sk.builtin.checkString(direction));
            Sk.builtin.pyCheckType("step", "number", Sk.builtin.checkNumber(step));

            const prom = hero.moveDirection(Sk.ffi.remapToJs(direction), Sk.ffi.remapToJs(step));

            var susp = new Sk.misceval.Suspension();

            susp.resume = function () {
                return Sk.builtin.none.none$;
            };

            susp.data = {
                type: "Sk.promise",
                promise: prom
            };

            return susp;
        });

        mod.moveXY = new Sk.builtin.func(function (x, y) {
            Sk.builtin.pyCheckArgs("moveXY", arguments, 2, 2);
            Sk.builtin.pyCheckType("x", "number", Sk.builtin.checkNumber(x));
            Sk.builtin.pyCheckType("y", "number", Sk.builtin.checkNumber(y));

            const prom = hero.moveXY(Sk.ffi.remapToJs(x), Sk.ffi.remapToJs(y))

            var susp = new Sk.misceval.Suspension();

            susp.resume = function () {
                if (err) {
                    throw new Sk.builtin.ValueError(err);
                } else {
                    return Sk.builtin.none.none$;
                }
            };

            susp.data = {
                type: "Sk.promise",
                promise: prom.then(function () {
                    err = "";
                }).catch(function (msg) {
                    err = msg;
                })
            };

            return susp;
        });

        mod.attack = new Sk.builtin.func(function (id) {
            Sk.builtin.pyCheckArgs("attack", arguments, 1, 1);
            const prom = hero.attack(Sk.ffi.remapToJs(id));

            var susp = new Sk.misceval.Suspension();

            susp.resume = function () {
                if (err) {
                    throw new Sk.builtin.ValueError(err);
                } else {
                    return Sk.builtin.none.none$;
                }
            };

            susp.data = {
                type: "Sk.promise",
                promise: prom.then(function () {
                    err = "";
                }).catch(function (msg) {
                    err = msg;
                })
            };

            return susp;
        });

        mod.say = new Sk.builtin.func(function (s) {
            Sk.builtin.pyCheckArgs("say", arguments, 1, 1);
            Sk.builtin.pyCheckType("s", "string", Sk.builtin.checkString(s));

            const prom = hero.say(Sk.ffi.remapToJs(s));

            var susp = new Sk.misceval.Suspension();

            susp.resume = function () {
                if (err) {
                    throw new Sk.builtin.ValueError(err);
                } else {
                    return Sk.builtin.none.none$;
                }
            };

            susp.data = {
                type: "Sk.promise",
                promise: prom.then(function () {
                    err = "";
                }).catch(function (msg) {
                    err = msg;
                })
            };

            return susp;
        });

        mod.findNearestEnemy = new Sk.builtin.func(function () {
            Sk.builtin.pyCheckArgs("findNearestEnemy", arguments, 0, 0);
            var id = hero.findNearestEnemy();
            if (id) {
                return new Sk.builtin.int_(id);
            } else {
                return Sk.builtin.none.none$;
            }
        });

        mod.distanceTo = new Sk.builtin.func(function (id) {
            Sk.builtin.pyCheckArgs("distanceTo", arguments, 1, 1);
            var distance = hero.distanceTo(Sk.ffi.remapToJs(id));
            if (distance === -1) {
                throw new Sk.builtin.ValueError("invalid id");
            } else {
                return new Sk.builtin.float_(distance);
            }
        });

        return mod;
    }
    `;


    const heroCode = `
from game import *

class Hero(object):
    def _moveDirection(self, direction, step):
        return moveDirection(direction, step)

    def move_up(self, step=1):
        return self._moveDirection("up", step=step)

    def move_down(self, step=1):
        return self._moveDirection("down", step=step)

    def move_right(self, step=1):
        return self._moveDirection("right", step=step)

    def move_left(self, step=1):
        return self._moveDirection("left", step=step)

    def move_xy(self, x, y):
        return moveXY(x, y)

    def attack(self, id):
        return attack(id)
    
    def say(self, s):
        return say(s)

    def find_nearest_enemy(self):
        return findNearestEnemy()

    def distance_to(self, id):
        return distanceTo(id);
`;

    const customModules = {"/hero/__init__.py": heroCode, "/hero/game.js": gameCore};

    function builtinRead(x) {
        if (customModules[x] !== undefined) {
            return customModules[x];
        }

        if (Sk.builtinFiles === undefined || Sk.builtinFiles["files"][x] === undefined)
            throw "File not found: " + x;
        return Sk.builtinFiles["files"][x];
    }

    function print(text) {
        console.log(text);
    }

    editor = ace.edit("editor");
    editor.setTheme("ace/theme/monokai");
    editor.getSession().setMode("ace/mode/python");
    Sk.onCodeLineChange = function(line){
        console.log('line:' + line);
        var l = codeEditor.codeLineMap[line];
        if(l){
            editor.gotoLine(l - 3);
        }
    }
    codeEditor.run = function(time) {
        editor.setReadOnly(true);
        var code = `import time
from hero import Hero
coder = Hero()
${editor.getValue()}`;
        Sk.python3 = true;
        Sk.builtin.KeyboardInterrupt = function (args) {
            var o;
            if (!(this instanceof Sk.builtin.KeyboardInterrupt)) {
                o = Object.create(Sk.builtin.KeyboardInterrupt.prototype);
                o.constructor.apply(o, arguments);
                return o;
            }
            Sk.builtin.BaseException.apply(this, arguments);
        };
        Sk.abstr.setUpInheritance("KeyboardInterrupt", Sk.builtin.KeyboardInterrupt, Sk.builtin.BaseException);

        Sk.configure({output: print, read: builtinRead, execLimit: time * 1000});

        var interruptHandler = function (susp) {
            if (Sk.hardInterrupt === true) {
                throw new Sk.builtin.KeyboardInterrupt('aborted execution');
            } else {
                return null; // should perform default action
            }
        };
        codeEditor.codeLineMap = [];
        var lines = code.split('\n');
        var code_arr = [];
        var insertLines = 0;
        for(var i = 0; i < lines.length; i++){
            let str = lines[i];
            var code_line = i + 1;
            var line = code_line + insertLines;
            code_arr[line] = str;
            codeEditor.codeLineMap[line + 1] = code_line;
            let index = str.lastIndexOf('while True:');
            if(index !== -1){
                var space = ' ';
                code_arr[line + 1] = space.repeat(index) + '    time.sleep(0.1)';
                codeEditor.codeLineMap[code_line + 1 + 1] = code_line + 1;
                insertLines++;
            }
        }
        code = code_arr.join('\n');
        code = code.replace(/\t/g, '    ')
        return Sk.misceval.asyncToPromise(function () {
            return Sk.importMainWithBody("<stdin>", false, code, true);
        }, {"*": interruptHandler});
    }

    codeEditor.stop = function() {
        Sk.configure({execLimit:0});
        editor.setReadOnly(false);
    }

    codeEditor.codeLines = function() {
        String.prototype.trim = function(){
            return this.replace(/^\s+|\s+$/g,'');
        };

        var count = 0;
        var code = codeEditor.getCode().match(/[^\r\n]+/g);
        for(var i = 0; i < code.length; i++) {
            if(code[i].trim() && code[i].trim()[0] != "#") {
                count ++;
            }
        }
        return count;
    }

    codeEditor.setCode = function(code) {
        return editor.setValue(code);
    }

    codeEditor.getCode = function(code) {
        return editor.getValue();
    }

    codeEditor.show = function(top, right, bottom, width){
        var ed = document.getElementById('editor');
        ed.style.display = 'block';
        ed.style.top = top.toFixed(1) + 'px';
        ed.style.right = right.toFixed(1) + 'px';
        ed.style.bottom = bottom.toFixed(1) + 'px';
        ed.style.width = width.toFixed(1) + 'px';
        editor.setFontSize(16);
    }

    codeEditor.insert = function(words){
        editor.insert(words);
    }

    codeEditor.hide = function(){
        var ed = document.getElementById('editor');
        ed.style.display = 'none';
    }

    codeEditor.clearSelection = function(){
        editor.clearSelection();
    }
})()