module.exports = function(grunt) {
  'use strict';

  grunt.loadNpmTasks('grunt-contrib-clean');
  grunt.loadNpmTasks('grunt-contrib-concat');
  grunt.loadNpmTasks('grunt-contrib-jshint');
  grunt.loadNpmTasks('grunt-contrib-uglify');
  grunt.loadNpmTasks('grunt-contrib-copy');
  grunt.loadNpmTasks('grunt-wiredep');
  grunt.loadNpmTasks('grunt-karma');

  grunt.registerTask('default', ['jshint', 'karma', 'wiredep', 'copy']);

  grunt.initConfig({

    pkg: grunt.file.readJSON('package.json'),
    bower: grunt.file.readJSON('.bowerrc'),

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
      }
    }

  });
};
