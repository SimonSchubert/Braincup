//
//  WrongChallengeAnswerFeedback.swift
//  braincup
//
//  Created by Simon Schubert on 26.12.19.
//  Copyright © 2019 Simon Schubert. All rights reserved.
//

import SwiftUI

struct CorrectChallengeAnswerFeedback: View {
    var solution: String
    var secret: String
    var url: String
    var back: () -> Void
    
    @State private var isSharePresented: Bool = false
    
    var body: some View {
        NavigationView {
            VStack {
                Text("Congratulation").font(.title)
                Text("Your solution '\(solution)' solved the challenge.").padding(.top, 12)
                if (secret != "") {
                    Text("Secret unveiled: \(secret)").padding(.top, 12)
                }
                Image("delivery")
                Button(action: { self.isSharePresented = true }) {
                    HStack {
                        Image("icons8-edit_link")
                        Text("Share challenge").frame(minWidth: 0, maxWidth: 160)
                    }
                }.buttonStyle(BackgroundButtonStyle()).padding(.top, 32).sheet(isPresented: $isSharePresented, content: {
                    ActivityViewController(activityItems: [URL(string: self.url)!])
                })
            }
            .navigationBarItems(leading: Button(action: { self.back()}){Image("back").foregroundColor(Color(hex: 0xFFED7354))})
        }.navigationViewStyle(StackNavigationViewStyle())
    }
}

struct CorrectChallengeAnswerFeedback_Previews: PreviewProvider {
    static var previews: some View {
        CorrectChallengeAnswerFeedback(solution: "abc", secret: "z", url: "", back: {})
    }
}
