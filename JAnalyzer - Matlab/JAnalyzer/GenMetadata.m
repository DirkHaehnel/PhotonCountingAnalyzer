function metadata = GenMetadata(Ex1,Ex2,Em1,Em2,Dist1,Dist2,Pinhole,Temp)

if (nargin > 5)
    metadata.LambdaEx1 = Ex1;
    metadata.LambdaEx2 = Ex2;
    metadata.LambdaEm1 = Em1;
    metadata.LambdaEm2 = Em2;
    metadata.Distance1 = Dist1;
    metadata.Distance2 = Dist2;
    metadata.Pinhole = Pinhole;
    metadata.Temperature = Temp;
else
    metadata.LambdaEx1 = Ex1;
    metadata.LambdaEm1 = Ex2;
    metadata.Distance1 = Em1;
    metadata.Pinhole = Em2;
    metadata.Temperature = Dist1;
end
