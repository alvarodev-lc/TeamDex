package es.upm.mssde.pokedex;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface GithubService {
    @GET("repos/{owner}/{repo}/contents/{path}")
    Call<GitHubContent> getChangelog(
            @Path("owner") String owner,
            @Path("repo") String repo,
            @Path("path") String path
    );

    class GitHubContent {
        public String content; // base64 encoded content
    }
}
