module.exports = function(grunt) {
  'use strict';

  grunt.loadNpmTasks('grunt-contrib-clean');
  grunt.loadNpmTasks('grunt-contrib-concat');
  grunt.loadNpmTasks('grunt-contrib-jshint');
  grunt.loadNpmTasks('grunt-contrib-uglify');
  grunt.loadNpmTasks('grunt-contrib-copy');
  grunt.loadNpmTasks('grunt-wiredep');

  grunt.registerTask('default', ['jshint', 'wiredep', 'copy']);
  grunt.registerTask('build', ['uglify', 'copy']);

  grunt.initConfig({

    pkg: grunt.file.readJSON('package.json'),
    bower: grunt.file.readJSON('.bowerrc'),

    config: {
      src: 'src',
      index: '<%= config.src %>/index.html',
      app: '<%= config.src %>/app',
      appJs: '<%= config.app %>/**/*.js',
      dist: 'dist',
    },

    wiredep: {
      task: {
        src: ['<%= config.index %>']
      }
    },

    copy: {
      dist: {
        files: [{
          expand: true,
          cwd: '<%= config.src %>',
          src: '**',
          dest: '<%= config.dist %>'
        }]
      }
    },

    jshint: {
      files: ['gruntfile.js', '<%= config.appJs %>']
    }
  });
};
