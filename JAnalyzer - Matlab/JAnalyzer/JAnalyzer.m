function JAnalyzer()
    path = mfilename('fullpath');
    javaaddpath(strcat(path, '.jar'));
    %javaaddpath('D:\Java\JAnalyzer\dist\JAnalyzer.jar');
    import janalyzer.*;
    ja = janalyzer.JA_Main(cd); 
    %ja.setVisible(1);
end