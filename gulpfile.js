var gulp = require('gulp');

var filesToMove = [
    './node_modules/zingchart/client/modules/*.*',
    './node_modules/zingchart/client/*.js'
];

function movejs() {
    return gulp.src(filesToMove, { base: './node_modules/zingchart/client/' })
        .pipe(gulp.dest('src/main/webapp/WEB-INF/resources/js/zingchart/'));
}

gulp.task('default', movejs);
