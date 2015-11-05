module.exports = function(grunt) {
  'use strict';

  grunt.loadNpmTasks('grunt-contrib-clean');
  grunt.loadNpmTasks('grunt-contrib-concat');
  grunt.loadNpmTasks('grunt-contrib-jshint');
  grunt.loadNpmTasks('grunt-contrib-uglify');
  grunt.loadNpmTasks('grunt-contrib-copy');
  grunt.loadNpmTasks('grunt-contrib-connect');
  grunt.loadNpmTasks('grunt-ng-constant');
  grunt.loadNpmTasks('grunt-wiredep');
  grunt.loadNpmTasks('grunt-karma');

  grunt.registerTask('default', ['ngconstant:prod', 'jshint', 'karma:unit', 'wiredep', 'copy']);
  grunt.registerTask('dev', ['ngconstant:dev', 'connect:server', 'karma:watch']);

  grunt.initConfig({

    pkg: grunt.file.readJSON('package.json'),
    bower: grunt.file.readJSON('.bowerrc'),

    ngconstant: {
      options: {
        wrap: "(function(angular) { 'use strict';\n\n{%= __ngModule %}\n\n})(window.angular);",
        name: 'termed.config',
        dest: 'src/app/config.js'
      },
      dev: {
        constants: {
          apiUrl: 'http://localhost:9999'
        }
      },
      prod: {
        constants: {
          apiUrl: ''
        }
      }
    },

    connect: {
      server: {
        port: 9999,
        base: 'src'
      }
    },

    wiredep: {
      task: {
        src: ['src/index.html']
      }
    },

    copy: {
      dist: {
        files: [{
          expand: true,
          cwd: 'src',
          src: '**',
          dest: 'dist'
        }]
      }
    },

    jshint: {
      files: ['gruntfile.js', 'src/app/**/*.js']
    },

    karma: {
      unit: {
        options: {
          frameworks: ['jasmine'],
          singleRun: true,
          browsers: ['PhantomJS'],
          files: [
            'src/lib/angular/angular.js',
            'src/lib/angular-mocks/angular-mocks.js',
            'test/**/*.js'
          ]
        }
      },
      watch: {
        options: {
          frameworks: ['jasmine'],
          singleRun: false,
          autoWatch: true,
          browsers: ['Chrome'],
          files: [
            'src/lib/angular/angular.js',
            'src/lib/angular-mocks/angular-mocks.js',
            'test/**/*.js'
          ]
        }
      }
    }

  });
};
