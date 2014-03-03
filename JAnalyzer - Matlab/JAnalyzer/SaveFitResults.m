function SaveFitResults(name, dc, w0, a0, triplet, c, velo, err, rangeX, UsedCurves, UsedBunchs, pd, p, expflag, bootstrap, para, bounds, pmin, pmax)

    if nargin < 19
        disp('not enough parameters');
        return;
    end
    
    FitResults = cell(1,1);
    idx = 1;
    Sbla = load(name);
    if (isfield(Sbla, 'FitResults') && iscell(Sbla.FitResults))
        FitResults = Sbla.FitResults;
        idx = length(FitResults) + 1;
    end
    FitParameters = struct();
    FitParameters.rangeX = rangeX;
    FitParameters.UsedCurves = UsedCurves;
    FitParameters.UsedBunchs = UsedBunchs;
    FitParameters.pd = pd;
    FitParameters.p = p;
    FitParameters.expflag = expflag;
    FitParameters.bootstrap = bootstrap;
    FitParameters.para = para;
    FitParameters.bounds = bounds;
    FitParameters.pmin = pmin;
    FitParameters.pmax = pmax;
    
    FitResults{idx}.time = datestr(now, 'dd-mmm-yyyy HH:MM:SS');
    FitResults{idx}.FitParameters = FitParameters;
    
    FitResults{idx}.dc = dc;
    FitResults{idx}.w0 = w0;
    FitResults{idx}.a0 = a0;
    FitResults{idx}.triplet = triplet;
    FitResults{idx}.c = c;
    FitResults{idx}.velo = velo;
    FitResults{idx}.err = err;

    save(name, 'FitResults', '-append');
return
