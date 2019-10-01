package io.stackroute;

import io.reactivex.rxjava3.core.Observable;
import io.stackroute.Model.GitUserRepos;

public class App 
{
    public static void main(String[] args)
    {

        Observable gitOperations = Observable.create(GenericFunctions.RequestObservable);

        gitOperations.map(GenericFunctions.ConvertStringToPOJO).subscribe(e -> {
            GitUserRepos[] data = (GitUserRepos[]) e;
            for(GitUserRepos repo: data) {
                System.out.println(repo.toString());
            }
        });
        System.out.println("SHOULD GET EXECUTED FIRST");

    }
}
