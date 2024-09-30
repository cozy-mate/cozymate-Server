import boto3
import requests
import os

repo = os.environ['GITHUB_REPOSITORY']
pr_number = os.environ['PR_NUMBER']
github_token = os.environ['GITHUB_TOKEN']
access_key = os.environ['REVIEW_AWS_ACCESS_KEY_ID']
secret_key = os.environ['REVIEW_AWS_SECRET_ACCESS_KEY']

def get_pr_diff():
    # GitHub API 엔드포인트
    api_url = f"https://api.github.com/repos/{repo}/pulls/{pr_number}"

    # GitHub API 요청 헤더
    headers = {
        "Authorization": f"Bearer {github_token}",
        "Accept": "application/vnd.github.diff"
    }

    # API 요청
    response = requests.get(api_url, headers=headers)

    if response.status_code == 200:
        return response.text
    else:
        raise Exception(f"Failed to fetch PR diff: {response.status_code}, {response.text}")


def analyze_with_bedrock(diff):
    bedrock = boto3.client('bedrock-runtime', region_name="us-east-1",
                           aws_access_key_id=access_key,
                           aws_secret_access_key=secret_key)

    formatted_prompt = f"Human: You're a senior backend engineer. Below there is a code diff please help me do a code review.\n\nFormat:\n- Numbering issues, specify file. Use markdown, headers, code blocks.\n- Suggest improvements/examples.\n- Be constructive.\n\nPR diff:\n\n{diff}\n\nProvide detailed review. Please answer to korean Assistant:"

    response = bedrock.converse(
        modelId='anthropic.claude-3-haiku-20240307-v1:0',  # 또는 다른 적절한 모델
        messages=[
            {
                'role': 'user',
                'content': [
                    {
                        'text': formatted_prompt
                    }
                ]
            }
        ],
        inferenceConfig={
            'maxTokens': 2000,
            'temperature': 0.7,
            'topP': 1,
        }
    )

    assistant_message = response['output']['message']['content'][0]['text']
    return assistant_message


def post_review(comment):
    # GitHub API 엔드포인트
    api_url = f"https://api.github.com/repos/{repo}/issues/{pr_number}/comments"

    # GitHub API 요청 헤더
    headers = {
        "Authorization": f"Bearer {github_token}",
        "Accept": "application/vnd.github-commitcomment.raw+json"
    }
    data = {
        "body": "# [REVIEW]\n" + comment
    }

    # API 요청
    response = requests.post(api_url, headers=headers, json=data)

    if response.status_code == 201:
        print(f"Comment posted success!!")
    else:
        print(f"Failed to post comment: {response.status_code}, {response.text}")


if __name__ == "__main__":
    diff = get_pr_diff()
    review_comments = analyze_with_bedrock(diff)
    post_review(review_comments)
